package com.ggukgguk.api.common.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TotalAndListPayload {

	private int total;
	private List<?> list;
}
