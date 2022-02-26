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
import io.github.emilyydev.emmyson.data.DataType;
import io.github.emilyydev.emmyson.data.JsonArray;
import io.github.emilyydev.emmyson.data.JsonObject;
import io.github.emilyydev.emmyson.data.JsonString;
import io.github.emilyydev.emmyson.util.Try;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class StandardJsonReaderBenchmark {

  private static String readResource(final String resource) throws IOException {
    try (final var in = StandardJsonReaderBenchmark.class.getResourceAsStream(resource)) {
      return new String(in.readAllBytes());
    }
  }

  private DataFactory dataFactory;
  private String simpleString;
  private String simpleObject;
  private String nestedObjectTree;
  private String mockMinified;
  private String mock;

  @Setup(Level.Trial)
  public void prepare() throws IOException {
    this.dataFactory = DataFactory.findDataFactory().orElseThrow();
    this.simpleString = readResource("simple-string.json");
    this.simpleObject = readResource("simple-object.json");
    this.nestedObjectTree = readResource("nested-object-tree.json");
    this.mockMinified = readResource("mock-minified.json");
    this.mock = readResource("mock.json");
  }

  @Benchmark
  public Try<JsonString> simpleString() {
    return this.dataFactory.read(this.simpleString, DataType.STRING);
  }

  @Benchmark
  public Try<JsonObject> simpleObject() {
    return this.dataFactory.read(this.simpleObject, DataType.OBJECT);
  }

  @Benchmark
  public Try<JsonObject> nestedObjectTree() {
    return this.dataFactory.read(this.nestedObjectTree, DataType.OBJECT);
  }

  @Benchmark
  public Try<JsonArray> mockMinified() {
    return this.dataFactory.read(this.mockMinified, DataType.ARRAY);
  }

  @Benchmark
  public Try<JsonArray> mock() {
    return this.dataFactory.read(this.mock, DataType.ARRAY);
  }
}
