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

import io.github.emilyydev.emmyson.data.JsonBoolean;
import net.kyori.examination.string.StringExaminer;

public final class JsonBooleanImpl implements JsonBoolean {

  private static final long serialVersionUID = -8719783296423918416L;

  public static final JsonBoolean TRUE = new JsonBooleanImpl(true);
  public static final JsonBoolean FALSE = new JsonBooleanImpl(false);

  private final boolean value;

  private JsonBooleanImpl(final boolean value) {
    this.value = value;
  }

  @Override
  public boolean booleanValue() {
    return this.value;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonBoolean)) { return false; }
    return this.value == ((JsonBoolean) other).booleanValue();
  }

  @Override
  public int hashCode() {
    return (this.value ? 1 : 0);
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }
}
