package com.nbdeyy.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentInfoVO implements Serializable {
    private DoctorDetailVO doctorDetail;
    private List<TimeSlotVO> timeSlotList;
}
