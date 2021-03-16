package io.opentelemetry.sdk.extension.trace.export;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompletableSpanExporter implements SpanExporter {

  private final List<CompletableResultCode> results = new ArrayList<>();

  private final List<SpanData> exported = new ArrayList<>();

  private volatile boolean succeeded;

  List<SpanData> getExported() {
    return exported;
  }

  void succeed() {
    succeeded = true;
    results.forEach(CompletableResultCode::succeed);
  }

  @Override
  public CompletableResultCode export(Collection<SpanData> spans) {
    exported.addAll(spans);
    if (succeeded) {
      return CompletableResultCode.ofSuccess();
    }
    CompletableResultCode result = new CompletableResultCode();
    results.add(result);
    return result;
  }

  @Override
  public CompletableResultCode flush() {
    if (succeeded) {
      return CompletableResultCode.ofSuccess();
    } else {
      return CompletableResultCode.ofFailure();
    }
  }

  @Override
  public CompletableResultCode shutdown() {
    return flush();
  }
}
