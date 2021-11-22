package com.gatech.ihi.app.facade;

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FHIRFacade {
    private final IGenericClient client;

    public Procedure getProcedure(String procedureId) {
        Bundle bundle = client.search().
                forResource(Procedure.class)
                .where(new TokenClientParam("_id").exactly().code(procedureId))
                .returnBundle(Bundle.class)
                .execute();
        List<Procedure> listOfProcedure = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            listOfProcedure.add((Procedure) entry.getResource());
        }

        return listOfProcedure.get(0);
    }

    public List<Schedule> getSchedule(String practitionerResourceId) {
        Bundle bundle = client.search().
                forResource(Schedule.class)
                .where(Schedule.ACTOR.hasId(practitionerResourceId))
                .returnBundle(Bundle.class)
                .execute();
        List<Schedule> listOfSchedule = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            listOfSchedule.add((Schedule) entry.getResource());
        }
        return listOfSchedule;
    }

    public Slot getTimeSlot(String timeSlotId) {
        Bundle bundle = (Bundle) client.search().forResource(Slot.class)
                .where(new TokenClientParam("_id").exactly().code(timeSlotId))
                .prettyPrint()
                .execute();

        List<Slot> listOfSlot = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            listOfSlot.add((Slot) entry.getResource());
        }

        return listOfSlot.get(0);
    }

    public List<Slot> findTimeSlotFromSchedule(String scheduleResourceId) {
        Bundle bundle = (Bundle) client.search().forResource(Slot.class)
                .where(new ReferenceClientParam("schedule").hasId(scheduleResourceId))
                .prettyPrint()
                .execute();
        List<Slot> listOfSlot = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            listOfSlot.add((Slot) entry.getResource());
        }
        return listOfSlot;
    }

    public Practitioner getPractitioner(String practitionerId) {
        Bundle resultBundle = client.search()
                .forResource(Practitioner.class)
                .and(new TokenClientParam("_id").exactly().code(practitionerId))
                .returnBundle(Bundle.class)
                .execute();
        List<Practitioner> listOfPractitioner = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : resultBundle.getEntry()) {
            listOfPractitioner.add((Practitioner) entry.getResource());
        }
        return listOfPractitioner.stream()
                .filter(practitioner -> practitioner.getIdElement().getIdPart().contains(practitionerId))
                .collect(Collectors.toList())
                .get(0);
    }

    public Patient getPatient(String patientResourceId) {
        Patient patient = client.read()
                .resource(Patient.class)
                .withId(patientResourceId)
                .execute();
        return patient;
    }

    public Appointment getAppointment(String appointmentResourceId) {
        Appointment appointment = client.read()
                .resource(Appointment.class)
                .withId(appointmentResourceId)
                .execute();
        return appointment;
    }

    public String createAppointment(String practitionerResourceId, String patientResourceId, String slotId, String procedureId) {
        Appointment appointment = new Appointment();
        appointment.addParticipant().getActor().setReference(practitionerResourceId);
        appointment.addParticipant().getActor().setReference(patientResourceId);
        appointment.addServiceType().addCoding().setCode(procedureId);
        appointment.addSlot().setReference(slotId);
        MethodOutcome outcome = client.create()
                .resource(appointment)
                .prettyPrint()
                .encodedJson()
                .execute();
        return outcome.getId().getValue();
    }

    public void updateAppointment(String practitionerResourceId, String patientResourceId, String procedureId, String appointmentResourceId,
                                  String slotId) {
        Appointment appointment = new Appointment();
        appointment.addParticipant().getActor().setReference(practitionerResourceId);
        appointment.addParticipant().getActor().setReference(patientResourceId);
        appointment.addServiceType().addCoding().setCode(procedureId);
        appointment.addSlot().setReference(slotId);
        appointment.setId(appointmentResourceId);
        client.update().resource(appointment).execute();
    }

    public void deleteAppointment(String appointmentResourceId) {
        client.delete().resourceById(new IdDt("Appointment", appointmentResourceId)).execute();
    }
}
