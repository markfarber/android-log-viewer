/*
 * Copyright 2011, 2015 Mikhail Lopatkin
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
package org.bitbucket.mlopatkin.android.logviewer.ui.bookmarks;

import org.bitbucket.mlopatkin.android.logviewer.bookmarks.BookmarkModel;
import org.bitbucket.mlopatkin.android.logviewer.ui.indexframe.AbstractIndexController;
import org.bitbucket.mlopatkin.android.logviewer.ui.indexframe.DaggerIndexFrameComponent;
import org.bitbucket.mlopatkin.android.logviewer.ui.indexframe.IndexController;
import org.bitbucket.mlopatkin.android.logviewer.ui.indexframe.IndexFrame;
import org.bitbucket.mlopatkin.android.logviewer.ui.indexframe.IndexFrameComponent;
import org.bitbucket.mlopatkin.android.logviewer.ui.indexframe.IndexFrameModule;
import org.bitbucket.mlopatkin.android.logviewer.ui.logtable.LogTable;
import org.bitbucket.mlopatkin.android.logviewer.ui.mainframe.MainFrameDependencies;
import org.bitbucket.mlopatkin.android.logviewer.ui.mainframe.MainFrameScoped;

import javax.inject.Inject;
import javax.inject.Named;

@MainFrameScoped
public class BookmarkController extends AbstractIndexController implements IndexController {

    private final LogTable mainLogTable;

    private final IndexFrame indexFrame;

    private final BookmarkModel.Observer bookmarkChangeObserver = new BookmarkModel.Observer() {

        @Override
        public void onBookmarkAdded() {
            if (!indexFrame.isVisible()) {
                showWindow();
            }
            redrawMainTable();
        }

        @Override
        public void onBookmarkRemoved() {
            redrawMainTable();
        }
    };

    @Inject
    public BookmarkController(MainFrameDependencies mainFrameDependencies,
                              BookmarkModel bookmarksModel,
                              BookmarksLogModelFilter logModelFilter,
                              @Named(MainFrameDependencies.FOR_MAIN_FRAME) LogTable mainLogTable,
                              BookmarkFramePopupMenu.Factory popupMenuFactory) {
        super(mainLogTable);
        this.mainLogTable = mainLogTable;

        bookmarksModel.asObservable().addObserver(bookmarkChangeObserver);

        IndexFrameComponent indexFrameComponent =
                DaggerIndexFrameComponent.builder()
                                          .mainFrameDependencies(mainFrameDependencies)
                                          .indexFrameModule(
                                                  new IndexFrameModule(this, popupMenuFactory, logModelFilter))
                                          .build();
        indexFrame = indexFrameComponent.createFrame();
        indexFrame.setTitle("Bookmarks");

    }

    private void redrawMainTable() {
        mainLogTable.repaint();
    }

    public void showWindow() {
        indexFrame.setVisible(true);
    }
}
