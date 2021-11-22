package com.gatech.ihi.app.facade;

import com.gatech.ihi.app.model.TimeSlot;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DataFacade {
    private final Map<String, String> users = ImmutableMap.of(
            "admin", "admin",
            "test", "test"
    );

    private final FHIRFacade fhirFacade;

    public com.gatech.ihi.app.model.Appointment createAppointment(String procedureId, String patientId, String practitionerId, String timeSlotId) {
        final Practitioner practitioner = fhirFacade.getPractitioner(practitionerId);
        final Patient patient = fhirFacade.getPatient(patientId);
        final String appointmentResourceId = fhirFacade.createAppointment(practitioner.getId(), patient.getId(), timeSlotId, procedureId);
        return getAppointment(appointmentResourceId);
    }

    public com.gatech.ihi.app.model.Appointment modifyAppointment(String appointmentResourceId, String patientId, String practitionerId, String procedureId, String timeSlotId) {
        final Practitioner practitioner = fhirFacade.getPractitioner(practitionerId);
        final Patient patient = fhirFacade.getPatient(patientId);
        fhirFacade.updateAppointment(practitioner.getId(), patient.getId(), procedureId, appointmentResourceId, timeSlotId);
        return getAppointment(appointmentResourceId);
    }

    public void deleteAppointment(String appointmentId) {
        fhirFacade.deleteAppointment(appointmentId);
    }

    public com.gatech.ihi.app.model.Appointment getAppointment(String appointmentId) {
        final Appointment fhirAppointment = fhirFacade.getAppointment(appointmentId);
        final String slotId = fhirAppointment.getSlot().get(0).getReferenceElement().getIdPart();
        final Slot fhirTimeSlot = fhirFacade.getTimeSlot(slotId);
//        if (fhirAppointment.getParticipant().size() == 0) {
//            return com.gatech.ihi.app.model.Appointment.builder()
//                    .appointmentId(fhirAppointment.getIdElement().getIdPart())
//                    .procedureId(fhirAppointment.getServiceType().get(0).getCoding().get(0).getCode())
//                    .patientId("UNKNOWN")
//                    .practitionerId("UNKNOWN")
//                    .startTime(fhirTimeSlot.getStart())
//                    .endTime(fhirTimeSlot.getEnd())
//                    .build();
//        } else if (fhirAppointment.getParticipant().size() == 1) {
//            final String practitionerId = fhirAppointment.getParticipant().get(0).getActor().getReferenceElement().getIdPart();
//            final Practitioner practitioner = fhirFacade.getPractitioner(practitionerId);
//            return com.gatech.ihi.app.model.Appointment.builder()
//                    .appointmentId(fhirAppointment.getIdElement().getIdPart())
//                    .procedureId(fhirAppointment.getServiceType().get(0).getCoding().get(0).getCode())
//                    .patientId("UNKNOWN")
//                    .practitionerId(practitioner.getIdElement().getIdPart())
//                    .startTime(fhirTimeSlot.getStart())
//                    .endTime(fhirTimeSlot.getEnd())
//                    .build();
//        }
        final Practitioner practitioner = fhirFacade.getPractitioner(fhirAppointment.getParticipant().get(0).getActor().getReferenceElement().getIdPart());
        final Patient patient = fhirFacade.getPatient(fhirAppointment.getParticipant().get(1).getActor().getReferenceElement().getIdPart());
        return com.gatech.ihi.app.model.Appointment.builder()
                .appointmentId(fhirAppointment.getIdElement().getIdPart())
                .procedureId(fhirAppointment.getServiceType().get(0).getCoding().get(0).getCode())
                .patientId(patient.getIdElement().getIdPart())
                .practitionerId(practitioner.getIdElement().getIdPart())
                .startTime(fhirTimeSlot.getStart())
                .endTime(fhirTimeSlot.getEnd())
                .build();
    }

    public com.gatech.ihi.app.model.Practitioner getPractitioner(String practitionerId) {
        final Practitioner fhirPractitioner = fhirFacade.getPractitioner(practitionerId);
        return com.gatech.ihi.app.model.Practitioner.builder()
                .name(fhirPractitioner.getName().get(0).getNameAsSingleString())
                .practitionerId(practitionerId)
                .build();
    }

    public com.gatech.ihi.app.model.Procedure getProcedure(String procedureId) {
        final Procedure fhirProcedure = fhirFacade.getProcedure(procedureId);
        return com.gatech.ihi.app.model.Procedure.builder()
                .procedureId(procedureId)
                .code(fhirProcedure.getCode().getCoding().get(0).getCode())
                .codeSystem(fhirProcedure.getCode().getCoding().get(0).getSystem())
                .codeDisplay(fhirProcedure.getCode().getCoding().get(0).getDisplay())
                .condition(fhirProcedure.getReasonReference().get(0).getDisplay())
                .build();
    }

    public com.gatech.ihi.app.model.Patient getPatient(String patientId) {
        final Patient fhirPatient = fhirFacade.getPatient(patientId);
        return com.gatech.ihi.app.model.Patient.builder()
                .name(patientId)
                .patientId(patientId)
                .build();
    }

    public List<TimeSlot> getAvailableTimeSlots(String practitionerId, Date start, Date end) {
        final List<Schedule> schedules = fhirFacade.getSchedule(practitionerId);
        final List<Slot> fhirTimeSlots =
                schedules.stream()
                        .flatMap(schedule -> fhirFacade.findTimeSlotFromSchedule(schedule.getId()).stream())
                        .collect(Collectors.toList());
        final List<TimeSlot> timeSlots = fhirTimeSlots.stream()
                .map(slot ->
                        TimeSlot.builder()
                                .timeSlotId(slot.getIdElement().getIdPart())
                                .startTime(slot.getStart())
                                .endTime(slot.getEnd())
                                .build())
                .filter(timeSlot -> !timeSlot.getStartTime().equals(timeSlot.getEndTime()))
                .collect(Collectors.toList());

        return timeSlots.stream()
                .filter(timeSlot -> (timeSlot.getStartTime().after(start) || timeSlot.getStartTime().equals(start))
                        && (timeSlot.getEndTime().before(end) || timeSlot.getEndTime().equals(end)))
                .collect(Collectors.toList());
    }

    public boolean login(String username, String password) {
        return users.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(username) && entry.getValue().equals(password));
    }
}

