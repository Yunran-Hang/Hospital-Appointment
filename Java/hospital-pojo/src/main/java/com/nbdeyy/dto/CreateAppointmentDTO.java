package com.nbdeyy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppointmentDTO {
    private Long doctorId;
    private LocalDate  workDate;
    private Integer period;
    private Long timeSlotId;
}
