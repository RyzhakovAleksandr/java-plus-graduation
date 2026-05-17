package ru.practicum.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CollectorGrpcService extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final KafkaUserActionProducer producer;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("Received user action: userId={}, eventId={}, actionType={}",
                request.getUserId(), request.getEventId(), request.getActionType());

        try {
            UserActionAvro avroMessage = convertToAvro(request);
            producer.send(avroMessage);

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();

            log.info("User action processed successfully");
        } catch (Exception e) {
            log.error("Error processing user action", e);
            responseObserver.onError(e);
        }
    }

    private UserActionAvro convertToAvro(UserActionProto proto) {
        ActionTypeAvro actionType = convertActionType(proto.getActionType());

        long timestamp = proto.getTimestamp().getSeconds() * 1000 +
                proto.getTimestamp().getNanos() / 1_000_000;

        return UserActionAvro.newBuilder()
                .setUserId(proto.getUserId())
                .setEventId(proto.getEventId())
                .setActionType(actionType)
                .setTimestamp(timestamp)
                .build();
    }

    private ActionTypeAvro convertActionType(ActionTypeProto protoType) {
        return switch (protoType) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> throw new IllegalArgumentException("Unknown action type: " + protoType);
        };
    }
}