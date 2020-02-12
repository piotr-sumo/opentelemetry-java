/*
 * Copyright 2020, OpenTelemetry Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opentelemetry.sdk.metrics;

import static com.google.common.truth.Truth.assertThat;

import io.opentelemetry.sdk.metrics.aggregator.Aggregator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link AbstractBoundInstrument}. */
@RunWith(JUnit4.class)
public class AbstractBoundInstrumentTest {
  @Mock private Aggregator aggregator;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void bindMapped() {
    TestBoundInstrument testBoundInstrument = new TestBoundInstrument(aggregator);
    assertThat(testBoundInstrument.bind()).isTrue();
    testBoundInstrument.unbind();
    assertThat(testBoundInstrument.bind()).isTrue();
    assertThat(testBoundInstrument.bind()).isTrue();
    testBoundInstrument.unbind();
    assertThat(testBoundInstrument.bind()).isTrue();
    testBoundInstrument.unbind();
    testBoundInstrument.unbind();
  }

  @Test
  public void tryUnmap_BoundInstrument() {
    TestBoundInstrument testBoundInstrument = new TestBoundInstrument(aggregator);
    assertThat(testBoundInstrument.bind()).isTrue();
    assertThat(testBoundInstrument.tryUnmap()).isFalse();
    testBoundInstrument.unbind();
    assertThat(testBoundInstrument.tryUnmap()).isTrue();
  }

  @Test
  public void tryUnmap_BoundInstrument_MultipleTimes() {
    TestBoundInstrument testBoundInstrument = new TestBoundInstrument(aggregator);
    assertThat(testBoundInstrument.bind()).isTrue();
    assertThat(testBoundInstrument.bind()).isTrue();
    assertThat(testBoundInstrument.bind()).isTrue();
    assertThat(testBoundInstrument.tryUnmap()).isFalse();
    testBoundInstrument.unbind();
    assertThat(testBoundInstrument.bind()).isTrue();
    assertThat(testBoundInstrument.tryUnmap()).isFalse();
    testBoundInstrument.unbind();
    assertThat(testBoundInstrument.tryUnmap()).isFalse();
    testBoundInstrument.unbind();
    assertThat(testBoundInstrument.tryUnmap()).isFalse();
    testBoundInstrument.unbind();
    assertThat(testBoundInstrument.tryUnmap()).isTrue();
  }

  @Test
  public void bind_ThenUnmap_ThenTryToBind() {
    TestBoundInstrument testBoundInstrument = new TestBoundInstrument(aggregator);
    assertThat(testBoundInstrument.bind()).isTrue();
    testBoundInstrument.unbind();
    assertThat(testBoundInstrument.tryUnmap()).isTrue();
    assertThat(testBoundInstrument.bind()).isFalse();
    testBoundInstrument.unbind();
  }

  @Test
  public void recordDoubleValue() {
    TestBoundInstrument testBoundInstrument = new TestBoundInstrument(aggregator);
    Mockito.verifyZeroInteractions(aggregator);
    Mockito.doNothing().when(aggregator).recordDouble(Mockito.anyDouble());
    testBoundInstrument.recordDouble(1.2);
    Mockito.verify(aggregator, Mockito.times(1)).recordDouble(1.2);
  }

  @Test
  public void recordLongValue() {
    TestBoundInstrument testBoundInstrument = new TestBoundInstrument(aggregator);
    Mockito.verifyZeroInteractions(aggregator);
    Mockito.doNothing().when(aggregator).recordLong(Mockito.anyLong());
    testBoundInstrument.recordLong(13);
    Mockito.verify(aggregator, Mockito.times(1)).recordLong(13);
  }

  private static final class TestBoundInstrument extends AbstractBoundInstrument {
    TestBoundInstrument(Aggregator aggregator) {
      super(aggregator);
    }
  }
}