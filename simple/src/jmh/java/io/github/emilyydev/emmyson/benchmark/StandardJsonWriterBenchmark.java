//
// Simple implementation for the EmmySON API
// Copyright (C) 2022  emilyy-dev
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package io.github.emilyydev.emmyson.benchmark;

import io.github.emilyydev.emmyson.data.DataFactory;
import io.github.emilyydev.emmyson.data.JsonData;
import io.github.emilyydev.emmyson.data.JsonObject;
import io.github.emilyydev.emmyson.data.JsonString;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class StandardJsonWriterBenchmark {

  private static final BigInteger BIG_ASS_NUMBER = BigInteger.valueOf(Long.MAX_VALUE)
      .multiply(BigInteger.TEN)
      .multiply(BigInteger.TWO);

  private DataFactory dataFactory;
  private JsonString simpleString;
  private JsonObject simpleObject;
  private JsonObject nestedObjectTree;

  @Setup(Level.Trial)
  @SuppressWarnings("unchecked")
  public void prepare() {
    this.dataFactory = DataFactory.findDataFactory().orElseThrow();
    this.simpleString = this.dataFactory.string("Hello, world!");
    this.simpleObject = this.dataFactory.objectOf(
        entry("one", this.dataFactory.string(" abc123~±α👨‍🦲")),
        entry("two", this.dataFactory.number(BIG_ASS_NUMBER)),
        entry("three", this.dataFactory.bool(true)),
        entry("four", this.dataFactory.nil())
    );

    final var simpleObjectValues = this.dataFactory.arrayOf(this.simpleObject.values());
    final var fifthEntry = entry("five", simpleObjectValues);

    this.nestedObjectTree = this.dataFactory.objectOf(
        concat(
            concat(this.simpleObject.entrySet().stream(), of(fifthEntry)),
            of(entry("nesting time", this.dataFactory.objectOf(
                concat(
                    concat(this.simpleObject.entrySet().stream(), of(fifthEntry)),
                    of(entry("nesting time", this.dataFactory.objectOf(
                        concat(this.simpleObject.entrySet().stream(), of(fifthEntry))
                    )))
                )
            )))
        )
    );
  }

  @Benchmark
  public String simpleString() {
    return write(this.simpleString);
  }

  @Benchmark
  public String simpleObject() {
    return write(this.simpleObject);
  }

  @Benchmark
  public String nestedObjectTree() {
    return write(this.nestedObjectTree);
  }

  private String write(final JsonData jsonData) {
    final StringBuilder buffer = new StringBuilder();
    this.dataFactory.write(buffer, jsonData);
    return buffer.toString();
  }
}
