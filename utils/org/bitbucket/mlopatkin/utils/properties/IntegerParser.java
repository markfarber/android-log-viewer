/*
 * Copyright 2011 Mikhail Lopatkin
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

package org.bitbucket.mlopatkin.utils.properties;

import com.google.common.base.Strings;

public class IntegerParser implements Parser<Integer> {

    @Override
    public Integer read(String value) {
        return Integer.valueOf(Strings.emptyToNull(value.trim()));
    }

    @Override
    public String write(Integer value) {
        return value.toString();
    }

}
