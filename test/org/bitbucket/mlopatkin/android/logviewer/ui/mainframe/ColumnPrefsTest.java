/*
 * Copyright 2018 Mikhail Lopatkin
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

package org.bitbucket.mlopatkin.android.logviewer.ui.mainframe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.bitbucket.mlopatkin.android.logviewer.config.ConfigStorage.InvalidJsonContentException;
import org.bitbucket.mlopatkin.android.logviewer.ui.logtable.Column;
import org.bitbucket.mlopatkin.android.logviewer.ui.logtable.TogglesModelTestUtils;
import org.junit.Assume;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ColumnPrefsTest {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void savedPrefsAreRestoredCorrectly() throws Exception {
        ColumnPrefs prefs = new ColumnPrefs();
        JsonElement jsonElement = ColumnPrefs.CLIENT.toJson(gson, prefs);
        ColumnPrefs deserializedPrefs = ColumnPrefs.CLIENT.fromJson(gson, jsonElement);

        assertThat(prefs, TogglesModelTestUtils
                .visibleColumns(equalTo(TogglesModelTestUtils.getVisibleColumns(deserializedPrefs))));
    }

    @Test(expected = InvalidJsonContentException.class)
    public void nullJsonThrows() throws Exception {
        ColumnPrefs.CLIENT.fromJson(gson, toJsonElement(""));
    }

    @Test(expected = InvalidJsonContentException.class)
    public void emptyJsonThrows() throws Exception {
        ColumnPrefs.CLIENT.fromJson(gson, toJsonElement("{}"));
    }

    @Test(expected = InvalidJsonContentException.class)
    public void emptyVisibleListThrows() throws Exception {
        ColumnPrefs.CLIENT.fromJson(gson, toJsonElement("{\"visible\": []}"));
    }

    @Test(expected = InvalidJsonContentException.class)
    public void missingMessageColumnThrows() throws Exception {
        ColumnPrefs.CLIENT.fromJson(gson, toJsonElement("{\"visible\": [APP_NAME, TIME]}"));
    }

    @Test
    public void havingOnlyMessageColumnIsValid() throws Exception {
        ColumnPrefs prefs = ColumnPrefs.CLIENT.fromJson(gson, toJsonElement("{\"visible\": [MESSAGE]}"));

        assertThat(prefs, TogglesModelTestUtils.visibleColumns(contains(Column.MESSAGE)));
    }

    @Test(expected = InvalidJsonContentException.class)
    public void havingIndexColumnIsNotValid() throws Exception {
        ColumnPrefs.CLIENT.fromJson(gson, toJsonElement("{\"visible\": [MESSAGE, INDEX]}"));
    }

    @Test
    public void allParsedColumnsAreVisible() throws Exception {
        ColumnPrefs prefs =
                ColumnPrefs.CLIENT.fromJson(gson, toJsonElement("{\"visible\": [MESSAGE, APP_NAME, TIME]}"));

        assertThat(prefs, TogglesModelTestUtils
                .visibleColumns(containsInAnyOrder(Column.MESSAGE, Column.APP_NAME, Column.TIME)));
    }

    @Test
    public void indexColumnIsNotAvailable() {
        ColumnPrefs prefs = new ColumnPrefs();

        assertFalse("Index column isn't available in MainFrame", prefs.isColumnAvailable(Column.INDEX));
    }

    @Test
    public void allButIndexColumnAreAvailable() {
        ColumnPrefs prefs = new ColumnPrefs();
        Set<Column> allButIndex = TogglesModelTestUtils.getMatchingColumns(c -> c != Column.INDEX);
        assertThat(TogglesModelTestUtils.getAvailableColumns(prefs), equalTo(allButIndex));
    }

    @Test
    public void makingColumnVisibleWorks() {
        ColumnPrefs prefs = new ColumnPrefs();
        Assume.assumeFalse(prefs.isColumnVisible(Column.TID));

        prefs.setColumnVisibility(Column.TID, true);

        assertTrue(prefs.isColumnVisible(Column.TID));
    }

    @Test
    public void makingColumnHiddenWorks() {
        ColumnPrefs prefs = new ColumnPrefs();
        Assume.assumeTrue(prefs.isColumnVisible(Column.PID));

        prefs.setColumnVisibility(Column.PID, false);

        assertFalse(prefs.isColumnVisible(Column.TID));
    }

    @Test
    public void messageColumnIsVisibleByDefault() {
        ColumnPrefs prefs = new ColumnPrefs();

        assertTrue(prefs.isColumnVisible(Column.MESSAGE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void messageColumnCannotBeHidden() {
        ColumnPrefs prefs = new ColumnPrefs();

        prefs.setColumnVisibility(Column.MESSAGE, false);
    }

    @Test
    public void indexColumnIsHiddenByDefault() {
        ColumnPrefs prefs = new ColumnPrefs();

        assertFalse(prefs.isColumnVisible(Column.INDEX));
    }

    @Test(expected = IllegalArgumentException.class)
    public void indexColumnCannotBeShown() {
        ColumnPrefs prefs = new ColumnPrefs();

        prefs.setColumnVisibility(Column.INDEX, true);
    }

    @Test
    public void indexColumnCanBeHidden() {
        ColumnPrefs prefs = new ColumnPrefs();

        prefs.setColumnVisibility(Column.INDEX, false);

        assertFalse(prefs.isColumnAvailable(Column.INDEX));
        assertFalse(prefs.isColumnVisible(Column.INDEX));
    }

    private JsonElement toJsonElement(String s) {
        return new JsonParser().parse(s);
    }

}
