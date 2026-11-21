package ma.oumouou.cqrsesaxon.commands.dtos;

import java.time.Instant;

public record AccountEventResponse(
        String aggregateIdentifier,
        long sequenceNumber,
        Instant timestamp,
        String type,
        Object payload
) {
}
