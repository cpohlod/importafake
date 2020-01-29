package com.importa.error;

import java.util.Set;

public class ImportaUnSupportedFieldPatchException extends RuntimeException {

    private static final long serialVersionUID = -9107108995227545249L;

	public ImportaUnSupportedFieldPatchException(Set<String> keys) {
        super("Field " + keys.toString() + " update is not allow.");
    }

}
