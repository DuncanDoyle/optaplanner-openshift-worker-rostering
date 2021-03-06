/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.openshift.employeerostering.gwtui.client.pages;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.optaplanner.openshift.employeerostering.gwtui.client.employee.EmployeeListPanel;
import org.optaplanner.openshift.employeerostering.gwtui.client.pages.employeeroster.EmployeeRosterPage;
import org.optaplanner.openshift.employeerostering.gwtui.client.pages.rotation.RotationPage;
import org.optaplanner.openshift.employeerostering.gwtui.client.pages.spotroster.SpotRosterPage;
import org.optaplanner.openshift.employeerostering.gwtui.client.skill.SkillListPanel;
import org.optaplanner.openshift.employeerostering.gwtui.client.spot.SpotListPanel;

import static org.optaplanner.openshift.employeerostering.gwtui.client.pages.Pages.Id.EMPLOYEES;
import static org.optaplanner.openshift.employeerostering.gwtui.client.pages.Pages.Id.EMPLOYEE_ROSTER;
import static org.optaplanner.openshift.employeerostering.gwtui.client.pages.Pages.Id.ROTATION;
import static org.optaplanner.openshift.employeerostering.gwtui.client.pages.Pages.Id.SKILLS;
import static org.optaplanner.openshift.employeerostering.gwtui.client.pages.Pages.Id.SPOTS;
import static org.optaplanner.openshift.employeerostering.gwtui.client.pages.Pages.Id.SPOT_ROSTER;

@Dependent
public class Pages {

    public enum Id {
        SKILLS,
        SPOT_ROSTER,
        EMPLOYEE_ROSTER,
        ROTATION,
        SPOTS,
        EMPLOYEES;
    }

    @Inject
    private ManagedInstance<SkillListPanel> skillsPage;

    @Inject
    private ManagedInstance<SpotListPanel> spotsPage;

    @Inject
    private ManagedInstance<EmployeeListPanel> employeesPage;

    @Inject
    private ManagedInstance<SpotRosterPage> spotRosterPage;

    @Inject
    private ManagedInstance<EmployeeRosterPage> employeeRosterPage;

    @Inject
    private ManagedInstance<RotationPage> rotationPage;

    private final Map<Id, LazyInit<? extends Page>> mapping = new HashMap<>();

    @PostConstruct
    public void init() {
        mapping.put(SKILLS, lazyInit(skillsPage));
        mapping.put(SPOTS, lazyInit(spotsPage));
        mapping.put(EMPLOYEES, lazyInit(employeesPage));
        mapping.put(SPOT_ROSTER, lazyInit(spotRosterPage));
        mapping.put(EMPLOYEE_ROSTER, lazyInit(employeeRosterPage));
        mapping.put(ROTATION, lazyInit(rotationPage));
    }

    public Page get(final Id id) {
        //FIXME: Improve error handling?
        return Optional.ofNullable(mapping.get(id)).map(s -> s.get()).orElseThrow(() -> new RuntimeException("Unmapped page " + id));
    }

    private <T> LazyInit<T> lazyInit(final ManagedInstance<T> managedInstance) {
        return new LazyInit<>(managedInstance);
    }

    private static class LazyInit<T> {

        private final ManagedInstance<T> managedInstance;

        private T instance;

        private LazyInit(final ManagedInstance<T> managedInstance) {
            this.managedInstance = managedInstance;
        }

        public T get() {

            if (instance == null) {
                instance = managedInstance.get();
            }

            return instance;
        }
    }
}
