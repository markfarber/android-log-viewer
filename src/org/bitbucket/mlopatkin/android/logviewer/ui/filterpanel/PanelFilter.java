/*
 * Copyright 2014 Mikhail Lopatkin
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

package org.bitbucket.mlopatkin.android.logviewer.ui.filterpanel;

/**
 * This interface must be implemented by the filter instance that needs to be displayed in filter panel.
 * <p/>
 * The {@link FilterPanelModel} talks to embedder via this interface.
 */
public interface PanelFilter extends PanelFilterView {

    void setEnabled(boolean enabled);

    void openFilterEditor();

    void delete();
}
