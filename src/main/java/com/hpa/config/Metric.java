package com.hpa.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Metric {

    String exception;

    String instance;

    String method;

    String job;

    String uri;

    String outcome;

    String status;


}
