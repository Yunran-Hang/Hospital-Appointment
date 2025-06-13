package com.nbdeyy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaVerifyDTO {
    private String captchaVerifyParam;
}
