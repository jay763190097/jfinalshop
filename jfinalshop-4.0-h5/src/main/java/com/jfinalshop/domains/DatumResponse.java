package com.jfinalshop.domains;

import java.util.List;

public class DatumResponse  {
	private Object datum;

	private Integer	code;

	private String	message;


	public DatumResponse() {
		super();
	}

	public DatumResponse(Object datum) {
		this.datum = datum;
	}

	public DatumResponse setDatum(Object datum) {
		this.datum = datum;
		return this;
	}

	public Object getDatum() {
		return datum;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
