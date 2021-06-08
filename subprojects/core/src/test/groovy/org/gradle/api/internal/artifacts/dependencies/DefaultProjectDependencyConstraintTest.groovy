/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.artifacts.dependencies

import org.gradle.api.internal.artifacts.DefaultProjectDependencyFactory
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParserFactory
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder
import org.gradle.api.internal.notations.ProjectDependencyFactory
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.initialization.ProjectAccessListener
import org.gradle.util.AttributeTestUtil
import org.gradle.util.TestUtil
import org.gradle.util.internal.GUtil
import spock.lang.Specification

class DefaultProjectDependencyConstraintTest extends Specification {

    def "can copy project dependency constraint" () {

        setup:
        def projectDummy = Mock(ProjectInternal) {
            getGroup() >> "org.example"
            getVersion() >> "0.0.1"
        }
        def projectFinder = Mock(ProjectFinder)
        def capabilityNotationParser = new CapabilityNotationParserFactory(false).create()
        def depFactory = new DefaultProjectDependencyFactory(Mock(ProjectAccessListener), TestUtil.instantiatorFactory().decorateLenient(), true, capabilityNotationParser, AttributeTestUtil.attributesFactory())
        ProjectDependencyFactory factory = new ProjectDependencyFactory(depFactory)

        final Map<String, Object> mapNotation = GUtil.map("path", ":foo:bar", "configuration", "compile", "transitive", false)
        projectFinder.getProject(':foo:bar') >> projectDummy
        def projectDependency = factory.createFromMap(projectFinder, mapNotation);
        DefaultProjectDependencyConstraint original = new DefaultProjectDependencyConstraint(projectDependency)
        original.force = true
        original.reason = "why not"
        DefaultProjectDependencyConstraint copy = original.copy()

        expect:
        copy.group == original.group
        copy.name == original.name
        copy.version == original.version
        copy.versionConstraint == original.versionConstraint
        copy.versionConstraint.preferredVersion == original.versionConstraint.preferredVersion
        copy.versionConstraint.rejectedVersions == original.versionConstraint.rejectedVersions
        copy.reason == original.reason
        copy.force == original.force
    }

    
}
