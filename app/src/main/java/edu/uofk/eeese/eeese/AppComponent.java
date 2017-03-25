/*
 * Copyright 2017 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese;

import com.squareup.picasso.Picasso;

import dagger.Component;
import edu.uofk.eeese.eeese.about.AboutComponent;
import edu.uofk.eeese.eeese.about.AboutModule;
import edu.uofk.eeese.eeese.data.HTTPModule;
import edu.uofk.eeese.eeese.data.backend.BackendModule;
import edu.uofk.eeese.eeese.data.database.DatabaseModule;
import edu.uofk.eeese.eeese.data.source.RepositoryModule;
import edu.uofk.eeese.eeese.data.sync.SyncComponent;
import edu.uofk.eeese.eeese.data.sync.SyncModule;
import edu.uofk.eeese.eeese.details.DetailsComponent;
import edu.uofk.eeese.eeese.details.DetailsModule;
import edu.uofk.eeese.eeese.di.scopes.ApplicationScope;
import edu.uofk.eeese.eeese.events.EventsComponent;
import edu.uofk.eeese.eeese.events.EventsModule;
import edu.uofk.eeese.eeese.projects.ProjectsComponent;
import edu.uofk.eeese.eeese.projects.ProjectsModule;

@ApplicationScope
@Component(modules = {
        AppModule.class,
        HTTPModule.class,
        BackendModule.class,
        DatabaseModule.class,
        RepositoryModule.class})
public interface AppComponent {
    ProjectsComponent projectsComponent(ProjectsModule module);

    DetailsComponent detailsComponent(DetailsModule module);

    AboutComponent aboutComponent(AboutModule module);

    EventsComponent eventsComponent(EventsModule module);

    SyncComponent syncComponent(SyncModule module);

    Picasso picassoInstance();
}
