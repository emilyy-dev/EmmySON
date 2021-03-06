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

package io.github.emilyydev.emmyson.test;

import io.github.emilyydev.emmyson.data.DataFactory;
import io.github.emilyydev.emmyson.data.DataType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandardApiTest {

  private static DataFactory dataFactory;

  @BeforeAll
  public static void prepare() {
    dataFactory = DataFactory.findDataFactory().orElseThrow();
  }

  @Test
  public void api_dataType() {
    assertSame(DataType.NULL, dataFactory.nil().type());
  }

  @Test
  public void api_dataAsType_valid() {
    assertTrue(dataFactory.nil().as(DataType.NULL).isSuccess());
  }

  @Test
  public void api_dataAsType_invalid() {
    assertTrue(dataFactory.nil().as(DataType.STRING).isFailure());
  }
}
