# Building the super project with Jenkins #
Building an RPM of Daisy Pipeline 2 on CentOS 6.7

JDK is set to Java 8.

## Step 1 ##
The first build step is to invoke a (normal) Maven (v3.2.2) build with the following goals and options:

`-pl '!modules/braille/pipeline-braille-utils/liblouis-utils/liblouis-utils, !cli, !updater/cli' clean install -DskipTests -Dinvoker.skip=true`

A potential problem with this is the shared maven cache (which isn't configured at this point). This could cause problems if snapshots are used concurrently.

Another potential problem could be
<pre>
	&lt;repository>
		&lt;id>sonatype-nexus-staging-not-released&lt;/id>
		&lt;name>Nexus Release Repository&lt;/name>
		&lt;url>http://oss.sonatype.org/content/groups/staging/&lt;/url>
	&lt;/repository>
</pre>
in assembly/pom.xml

Which is resolved first in maven, local cache or the above?

## Step 2 ##
The second build step is to invoke the following shell commands:

`cd assembly`

`/var/lib/jenkins/tools/hudson.model.JDK/JDK8/bin/java -classpath /var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven-3.2.2/boot/plexus-classworlds-2.5.1.jar -Dmaven.home=/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven-3.2.2 -Dclassworlds.conf=/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven-3.2.2/bin/m2.conf org.codehaus.plexus.classworlds.launcher.Launcher clean package -P rpm`

This is just calling Maven, but without the convenience of the Jenkins plugin (since the build doesn't support -f which Jenkins uses in this case) and without installing mvn on Jenkins. An obvious problem with this is that it will break easily if there is a change in the Jenkins configuration (the command to call Maven is simply copied from the command line output of the previous Maven step).

## Step 3 ##
The third step is to invoke the following shell commands:

`cd assembly`

`./gradlew promoteToStageRepo promoteToUtvRepo`

The purpose of this step is simply to get the rpm into Artifactory. This relies on a custom plugin which is not publicly available. Ironically, the Jenkins gradle plugin doesn't properly support using gradlew in combination with subfolder builds, which is why this step is also a shell command.