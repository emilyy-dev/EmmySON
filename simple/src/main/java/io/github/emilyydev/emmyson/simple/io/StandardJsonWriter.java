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

package io.github.emilyydev.emmyson.simple.io;

import io.github.emilyydev.emmyson.data.DataType;
import io.github.emilyydev.emmyson.data.JsonArray;
import io.github.emilyydev.emmyson.data.JsonBoolean;
import io.github.emilyydev.emmyson.data.JsonData;
import io.github.emilyydev.emmyson.data.JsonNull;
import io.github.emilyydev.emmyson.data.JsonNumber;
import io.github.emilyydev.emmyson.data.JsonObject;
import io.github.emilyydev.emmyson.data.JsonString;
import io.github.emilyydev.emmyson.io.JsonWriter;
import io.github.emilyydev.emmyson.simple.util.Stuff.Escapable;
import io.github.emilyydev.emmyson.simple.util.Stuff.Literal;
import io.github.emilyydev.emmyson.simple.util.Stuff.Tokens;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.PrimitiveIterator;

public final class StandardJsonWriter implements JsonWriter {

  private static BufferedWriter asBufferedWriter(final Appendable out) {
    if (out instanceof BufferedWriter) {
      return (BufferedWriter) out;
    } else if (out instanceof Writer) {
      return new BufferedWriter((Writer) out);
    } else {
      return new BufferedWriter(new AppendableWriter(out));
    }
  }

  private final BufferedWriter out;

  public StandardJsonWriter(final Appendable out) {
    this.out = asBufferedWriter(out);
  }

  @Override
  public void write(final JsonData jsonData) throws IOException { // sneakily throws with Try#getOrThrow()
    final DataType<? extends JsonData> type = jsonData.type();
    if (DataType.NULL.equals(type)) {
      jsonData.as(DataType.NULL).tryConsume(this::write).getOrThrow();
    } else if (DataType.BOOLEAN.equals(type)) {
      jsonData.as(DataType.BOOLEAN).tryConsume(this::write).getOrThrow();
    } else if (DataType.NUMBER.equals(type)) {
      jsonData.as(DataType.NUMBER).tryConsume(this::write).getOrThrow();
    } else if (DataType.STRING.equals(type)) {
      jsonData.as(DataType.STRING).tryConsume(this::write).getOrThrow();
    } else if (DataType.ARRAY.equals(type)) {
      jsonData.as(DataType.ARRAY).tryConsume(this::write).getOrThrow();
    } else if (DataType.OBJECT.equals(type)) {
      jsonData.as(DataType.OBJECT).tryConsume(this::write).getOrThrow();
    }
  }

  @Override
  public void write(final JsonNull jsonNull) throws IOException {
    this.out.write(Literal.NULL);
  }

  @Override
  public void write(final JsonBoolean jsonBoolean) throws IOException {
    this.out.write(jsonBoolean.booleanValue() ? Literal.TRUE : Literal.FALSE);
  }

  @Override
  public void write(final JsonNumber jsonNumber) throws IOException {
    this.out.write(jsonNumber.asNumber().toString());
  }

  @Override
  public void write(final JsonString jsonString) throws IOException {
    this.out.write(Tokens.QUOTE);

    final PrimitiveIterator.OfInt iterator = jsonString.chars().iterator();
    while (iterator.hasNext()) {
      Escapable.writeMatchingOrWrite(iterator.nextInt(), this.out);
    }

    this.out.write(Tokens.QUOTE);
  }

  @Override
  public void write(final JsonArray jsonArray) throws IOException {
    this.out.write(Tokens.BEGIN_ARRAY);

    final Iterator<JsonData> iterator = jsonArray.iterator();
    if (iterator.hasNext()) {
      write(iterator.next());
      while (iterator.hasNext()) {
        this.out.write(Tokens.SEPARATOR);
        write(iterator.next());
      }
    }

    this.out.write(Tokens.END_ARRAY);
  }

  @Override
  public void write(final JsonObject jsonObject) throws IOException {
    this.out.write(Tokens.BEGIN_OBJECT);

    final Iterator<Map.Entry<JsonString, JsonData>> iterator = jsonObject.entrySet().iterator();
    if (iterator.hasNext()) {
      final Map.Entry<JsonString, JsonData> first = iterator.next();
      write(first.getKey());
      this.out.write(Tokens.OBJECT_MAPPER);
      write(first.getValue());
      while (iterator.hasNext()) {
        this.out.write(Tokens.SEPARATOR);
        final Map.Entry<JsonString, JsonData> entry = iterator.next();
        write(entry.getKey());
        this.out.write(Tokens.OBJECT_MAPPER);
        write(entry.getValue());
      }
    }

    this.out.write(Tokens.END_OBJECT);
  }

  @Override
  public void flush() throws IOException {
    this.out.flush();
  }

  @Override
  public void close() throws IOException {
    this.out.close();
  }
}
