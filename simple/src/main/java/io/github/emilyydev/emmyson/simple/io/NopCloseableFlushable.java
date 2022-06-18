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

import java.io.Closeable;
import java.io.Flushable;

final class NopCloseableFlushable implements Closeable, Flushable {

  static final NopCloseableFlushable INSTANCE = new NopCloseableFlushable();

  private NopCloseableFlushable() {
  }

  @Override
  public void close() {
  }

  @Override
  public void flush() {
  }
}
