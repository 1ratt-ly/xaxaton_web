package com.hackaton.website.exception;

public class TaxRateNotFoundException extends RuntimeException {
    public TaxRateNotFoundException(String message) { super(message); }
}