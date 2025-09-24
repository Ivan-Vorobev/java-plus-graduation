package ru.practicum.ewm.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.handler.RecommendationsHandler;
import ru.practicum.ewm.stats.services.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.messages.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.messages.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.messages.UserPredictionsRequestProto;
import ru.practicum.ewm.stats.messages.RecommendedEventProto;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final RecommendationsHandler handler;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Request for recommendations: {}", request);
            handler.getRecommendationsForUser(request).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error retrieving recommendations: {}", e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Request to get similar events for the specified: {}", request);
            handler.getSimilarEvents(request).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error retrieving related events: {}", e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            log.info("Request to get the sum of weights for each event: {}", request);
            handler.getInteractionsCount(request).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error getting event weight sum: {}", e.getMessage(), e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}