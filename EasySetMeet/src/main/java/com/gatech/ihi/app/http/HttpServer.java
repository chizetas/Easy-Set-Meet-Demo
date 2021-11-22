package com.gatech.ihi.app.http;

import com.gatech.ihi.app.facade.DataFacade;
import com.gatech.ihi.app.guice.ProdGuiceModule;
import com.gatech.ihi.app.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static spark.Spark.*;

@Log4j2
public class HttpServer {
    private static String DATE_STRING_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_STRING_FORMAT);
    private static Gson GSON = new GsonBuilder()
            .setDateFormat(DATE_STRING_FORMAT)
            .setPrettyPrinting()
            .create();

    static {
        DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        // == Init ==
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new ProdGuiceModule());
        final DataFacade dataFacade = injector.getInstance(DataFacade.class);

        // == Server ==
        port(8080);

        log.info("Server Starting...");

        enableCORS("*", "*", "*");

        // == Endpoint ==
        get("/", (req, res) -> "Hello World");

        // == Practitioner ==
        get("/practitioner/:id/timeslots", (req, res) -> {
            final String id = req.params("id");
            final Date start = DATE_FORMATTER.parse(req.queryParams("start"));
            final Date end = DATE_FORMATTER.parse(req.queryParams("end"));
            final List<TimeSlot> timeSlots = dataFacade.getAvailableTimeSlots(id, start, end);
            return GSON.toJson(timeSlots);
        });

        get("/practitioner/:id", (req, res) -> {
            final String id = req.params("id");
            final Practitioner practitioner = dataFacade.getPractitioner(id);
            return GSON.toJson(practitioner);
        });

        // == Procedure ==
        get("/procedure/:id", (req, res) -> {
            final String id = req.params("id");
            final Procedure procedure = dataFacade.getProcedure(id);
            return GSON.toJson(procedure);
        });

        // == Patient ==
        get("/patient/:id", (req, res) -> {
            final String id = req.params("id");
            final Patient patient = dataFacade.getPatient(id);
            return GSON.toJson(patient);
        });

        // == Appointment ==
        get("/appointment/:id", (req, res) -> {
            final String id = req.params("id");
            final Appointment appointment = dataFacade.getAppointment(id);
            return GSON.toJson(appointment);
        });

        delete("/appointment/:id", (req, res) -> {
            final String id = req.params("id");
            dataFacade.deleteAppointment(id);
            return GSON.toJson("");
        });

        put("/appointment/:id", (req, res) -> {
            final String id = req.params("id");
            final AppointmentWithoutId requestAppointment;
            requestAppointment = GSON.fromJson(req.body(), AppointmentWithoutId.class);
            final Appointment appointment =
                    dataFacade.modifyAppointment(
                            id,
                            requestAppointment.getPatientId(),
                            requestAppointment.getPractitionerId(),
                            requestAppointment.getProcedureId(),
                            requestAppointment.getTimeSlotId()
                    );
            return GSON.toJson(appointment);
        });

        post("/appointment", (req, res) -> {
            final AppointmentWithoutId requestAppointment;
            requestAppointment = GSON.fromJson(req.body(), AppointmentWithoutId.class);
            final Appointment appointment =
                    dataFacade.createAppointment(
                            requestAppointment.getProcedureId(),
                            requestAppointment.getPatientId(),
                            requestAppointment.getPractitionerId(),
                            requestAppointment.getTimeSlotId()
                    );
            return GSON.toJson(appointment);
        });

        // == Auth ==
        get("/user/login", (req, res) -> {
            final String username = req.queryParams("email");
            final String password = req.queryParams("password");
            final boolean login = dataFacade.login(username, password);
            if (login) {
                res.status(200);
                return "Success";
            } else {
                res.status(403);
                return "Failed";
            }
        });
    }

    // Enables CORS on requests. This method is an initialization method and should be called once.
    private static void enableCORS(final String origin, final String methods, final String headers) {

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            // Note: this may or may not be necessary in your particular application
            response.type("application/json");
        });
    }
}
