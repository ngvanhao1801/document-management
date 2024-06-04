package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassword {

	@NotEmpty
	@Length(min = 6)
	private String newPassword;

	@NotEmpty
	@Length(min = 6)
	private String confirmPassword;

}
