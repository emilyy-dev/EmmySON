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

public abstract class JsonBooleanImpl implements JsonBoolean {

  public static JsonBoolean get(final boolean b) {
    return b ? JsonTrue.INSTANCE : JsonFalse.INSTANCE;
  }

  private JsonBooleanImpl() { }

  @Override
  public boolean equals(final Object other) {
    if (this == other) { return true; }
    if (!(other instanceof JsonBoolean)) { return false; }
    return this.booleanValue() == ((JsonBoolean) other).booleanValue();
  }

  @Override
  public int hashCode() {
    return (this.booleanValue() ? 1 : 0);
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }

  private static final class JsonTrue extends JsonBooleanImpl {

    private static final JsonBoolean INSTANCE = new JsonTrue();

    private static final long serialVersionUID = 8908029280411688563L;

    @Override
    public boolean booleanValue() {
      return true;
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }

  private static final class JsonFalse extends JsonBooleanImpl {

    private static final JsonBoolean INSTANCE = new JsonFalse();

    private static final long serialVersionUID = -6619640628981400934L;

    private JsonFalse() {
      super();
    }

    @Override
    public boolean booleanValue() {
      return false;
    }

    private Object readResolve() {
      return INSTANCE;
    }
  }
}
