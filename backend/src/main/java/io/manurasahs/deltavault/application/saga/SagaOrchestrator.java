package io.manurasahs.deltavault.application.saga;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.manurasahs.deltavault.application.saga.step.SagaStep;
import io.manurasahs.deltavault.domain.metadata.model.FileMetadata;
import io.manurasahs.deltavault.domain.saga.model.Saga;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

@Service
public class SagaOrchestrator
{

    private final List<SagaStep> steps;

    public SagaOrchestrator(List<SagaStep> stepList)
    {
        this.steps = stepList.stream()
            .sorted(Comparator.comparingInt(step -> step.stepName().executionOrder()))
            .toList();
    }

    public void startSaga(@Nonnull FileMetadata metadata, @Nonnull byte[] fileContent)
    {
        var executed = new ArrayList<SagaStep>();
        var saga = new Saga(metadata, fileContent);
        try
        {
            steps.forEach(sagaStep -> {
                sagaStep.execute(saga);
                executed.add(sagaStep);
            });
        }
        catch (Exception e)
        {
            executed.reversed().forEach(sagaStep -> sagaStep.compensate(saga));
            throw new RuntimeException(STR."Saga failed: \{e.getMessage()}", e);
        }
    }
}