package com.gatech.ihi.app.facade;

import com.gatech.ihi.app.guice.LocalGuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Procedure;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Slot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.ResourceType.*;

class FHIRFacadeFunctionalTest {
    private static Injector injector;
    private static FHIRFacade fhirFacade;

    @BeforeAll
    public static void init() {
        injector = Guice.createInjector(Stage.PRODUCTION, new LocalGuiceModule());
    }

    @BeforeEach
    public void setup() {
        fhirFacade = injector.getInstance(FHIRFacade.class);
    }

    @Test
    public void testGetProcedure() {
        final String id = "2600004";
        final Procedure procedure = fhirFacade.getProcedure(id);
        assertThat(procedure.getResourceType()).isEqualTo(Procedure);
        assertThat(procedure.getIdElement().getIdPart()).isEqualTo(id);
    }

    @Test
    public void testGetSchedule() {
        final String practitionerResourceId = "1896012";
        final List<Schedule> listOfSchedule = fhirFacade.getSchedule(practitionerResourceId);
        assertThat(listOfSchedule.get(0).getResourceType()).isEqualTo(ResourceType.Schedule);
    }

    @Test
    public void testFindTimeSlotFromSchedule() {
        final String scheduleResourceId = "1896788";
        final List<Slot> listOfSlot = fhirFacade.findTimeSlotFromSchedule(scheduleResourceId);
        assertThat(listOfSlot.get(0).getResourceType()).isEqualTo(ResourceType.Slot);
    }

    @Test
    public void testGetPractitioner() {
        final String practitionerId = "1896012";
        final Practitioner practitioner = fhirFacade.getPractitioner(practitionerId);
        assertThat(practitioner.getName().get(0).getNameAsSingleString()).isEqualTo("Dr James Mokoena");
        assertThat(practitioner.getResourceType()).isEqualTo(Practitioner);
    }

    @Test
    public void testGetPatient() {
        final String patientId = "1539621";
        final Patient patient = fhirFacade.getPatient(patientId);
        assertThat(patient.getBirthDate()).isEqualTo("1924-10-10");
        assertThat(patient.getResourceType()).isEqualTo(Patient);
    }

    @Test
    public void testGetAppointment() {
        final String appointmentResourceId = "1942551";
        final Appointment appointment = fhirFacade.getAppointment(appointmentResourceId);
        assertThat(appointment.getResourceType()).isEqualTo(Appointment);
        assertThat(appointment.getIdElement().getIdPart()).isEqualTo(appointmentResourceId);
    }

    @Test
    public void testGetTimeSlot() {
        final String slotId = "2509668";
        final Slot slot = fhirFacade.getTimeSlot(slotId);
        assertThat(slot.getIdElement().getIdPart()).isEqualTo(slotId);
    }

    //create new appointment and then get the appointment
    @Test
    public void testCreateAppointment() {
        final String practitionerId = "1896012";
        final String procedureId = "2600004";
        final String patientId = "1539621";
        final List<Slot> listOfSlot = fhirFacade.findTimeSlotFromSchedule("272563");
        final Practitioner practitioner = fhirFacade.getPractitioner(practitionerId);
        final Patient patient = fhirFacade.getPatient(patientId);
        final Slot slot = listOfSlot.get(0);
        final String appointmentResourceId = fhirFacade.createAppointment(practitioner.getId(), patient.getId(), slot.getId(), procedureId);
        final Appointment appointment = fhirFacade.getAppointment(appointmentResourceId);
        assertThat(appointment.getServiceType().get(0).getCoding().get(0).getCode()).isEqualTo(procedureId);
        assertThat(appointment.getParticipant().get(0).getActor().getReferenceElement().getIdPart()).contains(practitionerId);
        assertThat(appointment.getParticipant().get(1).getActor().getReferenceElement().getIdPart()).contains(patientId);
    }

    //update an existed appointment and then get the appointment
    @Test
    public void testUpdateAppointment() {
        final String appointmentResourceId = "1942551";
        final String practitionerId = "";
        final String patientId = "1539621";
        final String procedureId = "2600004";
        final Practitioner practitioner = fhirFacade.getPractitioner(practitionerId);
        final Patient patient = fhirFacade.getPatient(patientId);
        final List<Slot> listOfSlot = fhirFacade.findTimeSlotFromSchedule("1896788");
        final Slot slot = listOfSlot.get(0);
        fhirFacade.updateAppointment(practitioner.getId(), patient.getId(), procedureId, appointmentResourceId, slot.getId());
        final Appointment appointment = fhirFacade.getAppointment(appointmentResourceId);
        assertThat(appointment.getServiceType().get(0).getCoding().get(0).getCode()).isEqualTo(procedureId);
        assertThat(appointment.getParticipant().get(0).getActor().getReferenceElement().getIdPart()).contains(practitionerId);
        assertThat(appointment.getParticipant().get(1).getActor().getReferenceElement().getIdPart()).contains(patientId);
    }

    @Test
    public void testDeleteAppointment() {
        final String appointmentResourceId = "2614993";
        fhirFacade.deleteAppointment(appointmentResourceId);
    }
}