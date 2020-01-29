package com.importa.error;

public class ImportaNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -570094041918743456L;

	public ImportaNotFoundException(String email) {
        super("email not found : " + email);
    }

}
