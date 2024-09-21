package mo.khodakov.webs.rest.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NO_ACTIVE_DATABASE("No active database"),
    FILE_NOT_FOUND("File not found"),
    INVALID_JSON("Invalid JSON"),
    PATH_NOT_FOUND("Filepath not found"),
    FILE_CREATION_FAILED("File creation failed"),
    FILE_ALREADY_EXISTS("File already exists");
    private final String data;
}
