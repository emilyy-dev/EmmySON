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

package io.github.emilyydev.emmyson.simple.data;

import io.github.emilyydev.emmyson.data.JsonNumber;
import net.kyori.examination.string.StringExaminer;

public final class JsonNumberImpl implements JsonNumber {

  private static final long serialVersionUID = 3198988074005737868L;

  private final Number number;

  public JsonNumberImpl(final Number number) {
    this.number = number;
  }

  @Override
  public long longValue() {
    return this.number.longValue();
  }

  @Override
  public double doubleValue() {
    return this.number.doubleValue();
  }

  @Override
  public Number asNumber() {
    return this.number;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonNumber)) { return false; }
    return asNumber().equals(((JsonNumber) other).asNumber());
  }

  @Override
  public int hashCode() {
    return this.number.hashCode();
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }
}
