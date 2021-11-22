package com.gatech.ihi.app.facade;

import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import com.gatech.ihi.app.guice.LocalGuiceModule;
import com.gatech.ihi.app.model.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataFacadeFunctionalTest {
    private static Injector injector;
    private static DataFacade dataFacade;
    private static String DATE_STRING_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_STRING_FORMAT);

    @BeforeAll
    public static void init() {
        injector = Guice.createInjector(Stage.PRODUCTION, new LocalGuiceModule());
    }

    @BeforeEach
    public void setup() {
        DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
        dataFacade = injector.getInstance(DataFacade.class);
    }

    @Test
    public void testGetAvailableTimeSlotsWithOneSlotOnly() throws ParseException {
        final String practitionerId = "1896013";
        final Date start = DATE_FORMATTER.parse("2019-07-10T14:00:00Z");
        final Date end = DATE_FORMATTER.parse("2019-07-10T15:00:00Z");
        final List<TimeSlot> timeSlots = dataFacade.getAvailableTimeSlots(practitionerId, start, end);

        assertThat(timeSlots.stream().map(TimeSlot::getTimeSlotId)).contains("1942575");
    }

    @Test
    public void testGetAvailableTimeSlotsWithAllSlots() throws ParseException {
        final String practitionerId = "1896013";
        final Date start = DATE_FORMATTER.parse("2019-01-06T14:00:00Z");
        final Date end = DATE_FORMATTER.parse("2019-12-06T15:00:00Z");
        final List<TimeSlot> timeSlots = dataFacade.getAvailableTimeSlots(practitionerId, start, end);

        assertThat(timeSlots.stream().map(TimeSlot::getTimeSlotId)).contains("1942575", "1901989", "1901987");
    }

    @Test
    public void testGetPractitioner() {
        final String practitionerId = "1896012";
        final Practitioner practitioner = dataFacade.getPractitioner(practitionerId);

        assertThat(practitioner.getName()).isEqualTo("Dr James Mokoena");
        assertThat(practitioner.getPractitionerId()).isEqualTo(practitionerId);
    }

    @Test
    public void testGetProcedure() {
        final String procedureId = "2600004";
        final Procedure procedure = dataFacade.getProcedure(procedureId);

        assertThat(procedure.getProcedureId()).isEqualTo(procedureId);
        assertThat(procedure.getCode()).isEqualTo("305428000");
        assertThat(procedure.getCodeSystem()).isEqualTo("http://snomed.info/sct");
        assertThat(procedure.getCodeDisplay()).isEqualTo("Admission to orthopedic department");
        assertThat(procedure.getCondition()).isEqualTo("Fracture of clavicle");
    }

    @Test
    public void testGetPatient() {
        final String patientId = "1539621";
        final Patient patient = dataFacade.getPatient(patientId);

        assertThat(patient.getName()).isEqualTo(patientId);
        assertThat(patient.getPatientId()).isEqualTo(patientId);
    }

    @Test
    public void testCreateAppointment() throws ParseException {
        final String procedureId = "2600004";
        final String practitionerId = "1896013";
        final String patientId = "1539621";
        final Date start = DATE_FORMATTER.parse("2019-07-10T14:00:00Z");
        final Date end = DATE_FORMATTER.parse("2019-07-10T15:00:00Z");
        final List<TimeSlot> timeSlots = dataFacade.getAvailableTimeSlots(practitionerId, start, end);
        final Appointment appointment = dataFacade.createAppointment(procedureId, patientId, practitionerId, timeSlots.get(0).getTimeSlotId());

        assertThat(appointment.getAppointmentId()).isNotNull();
        assertThat(appointment.getProcedureId()).isEqualTo(procedureId);
        assertThat(appointment.getPatientId()).isEqualTo(patientId);
        assertThat(appointment.getPractitionerId()).isEqualTo(practitionerId);
        assertThat(appointment.getStartTime()).isEqualTo(start);
        assertThat(appointment.getEndTime()).isEqualTo(end);
    }

    @Test
    public void testAppointmentFlow() throws ParseException {
        // == Input ==
        final String procedureId = "2600004";
        final String practitionerId = "1896013";
        final String patientId = "1539621";
        Date start = DATE_FORMATTER.parse("2019-07-10T14:00:00Z");
        Date end = DATE_FORMATTER.parse("2019-07-10T15:00:00Z");
        TimeSlot timeSlot = dataFacade.getAvailableTimeSlots(practitionerId, start, end).get(0);

        // == Create an appointment ==
        Appointment appointment = dataFacade.createAppointment(procedureId, patientId, practitionerId, timeSlot.getTimeSlotId());
        final String appointmentId = appointment.getAppointmentId();
        assertThat(appointment.getAppointmentId()).isNotNull();
        assertThat(appointment.getPractitionerId()).isEqualTo(practitionerId);
        assertThat(appointment.getPatientId()).isEqualTo(patientId);
        assertThat(appointment.getProcedureId()).isEqualTo(procedureId);
        assertThat(appointment.getStartTime()).isEqualTo(start);
        assertThat(appointment.getEndTime()).isEqualTo(end);

        // == Set new time ==
        start = DATE_FORMATTER.parse("2019-05-06T13:00:00Z");
        end = DATE_FORMATTER.parse("2019-05-06T14:00:00Z");
        timeSlot = dataFacade.getAvailableTimeSlots(practitionerId, start, end).get(0);

        // == Update the appointment ==
        appointment = dataFacade.modifyAppointment(appointmentId, patientId, practitionerId, procedureId, timeSlot.getTimeSlotId());
        assertThat(appointment.getAppointmentId()).isEqualTo(appointmentId);
        assertThat(appointment.getPractitionerId()).isEqualTo(practitionerId);
        assertThat(appointment.getPatientId()).isEqualTo(patientId);
        assertThat(appointment.getProcedureId()).isEqualTo(procedureId);
        assertThat(appointment.getStartTime()).isEqualTo(start);
        assertThat(appointment.getEndTime()).isEqualTo(end);

        // == Delete the appointment ==
        dataFacade.deleteAppointment(appointmentId);

        // == Get tje appointment ==
        assertThatThrownBy(() -> dataFacade.getAppointment(appointmentId)).isInstanceOf(ResourceGoneException.class);
    }

    @Test
    public void testLoginSuccess() {
        assertThat(dataFacade.login("admin", "admin")).isTrue();
        assertThat(dataFacade.login("test", "test")).isTrue();

        assertThat(dataFacade.login("admin", "random")).isFalse();
        assertThat(dataFacade.login("random", "random")).isFalse();
        assertThat(dataFacade.login("admin", "test")).isFalse();
    }
}