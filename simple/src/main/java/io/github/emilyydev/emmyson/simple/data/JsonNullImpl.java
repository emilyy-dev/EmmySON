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

import io.github.emilyydev.emmyson.data.JsonNull;
import net.kyori.examination.string.StringExaminer;

public final class JsonNullImpl implements JsonNull {

  public static final JsonNull INSTANCE = new JsonNullImpl();

  private static final long serialVersionUID = 7880251467620939467L;

  private JsonNullImpl() { }

  @Override
  public boolean equals(final Object other) {
    return other instanceof JsonNull;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public String toString() {
    return examine(StringExaminer.simpleEscaping());
  }

  private Object readResolve() {
    return INSTANCE;
  }
}
