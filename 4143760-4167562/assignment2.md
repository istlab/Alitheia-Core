# IN4189 Software Reengineering - Testing and Refactoring Report
By Martijn den Hoedt - 4143760 and Anton Bouter - 4167562

## Tests

For the following classes we made unit tests. These tests can be found in packages with corresponding names in the `src/test/java` folder:

 - `SchedulerStats` in package `eu.sqooss.service.scheduler`
 - `InMemoryDirectory` in package `eu.sqooss.service.fds`
 - `InMemoryCheckoutImpl` in package `eu.sqooss.impl.service.fds`

## Refactoring

### Single Responsibility Principle

As pointed out in the previous report, the `GitUpdater` class in the `eu.sqooss.plugins.updater.git` package was an enormous file that had many different and unrelated methods. This class violates the Single Responsibility, because this method does not have a single responsibility, because it is responsible for many more things, since its methods are unrelated. This violation causes the code to be more difficult to understand, and therefore to maintain. Splitting the `GitUpdater` class in more classes, such that each class only has related methods, and therefore a single responsibility, will resolve the violation.

We have split this class into four separate classes: `GitUpdater`, `GitProcessor`, `GitFileManager` and `GitMessageHandler`. The `GitUpdater` class contains the high-level methods responsible for updating the database, while `GitProcessor` is responsible for processing revisions to find e.g. branching info. Finally, the `GitFileManager` contains all methods that involve moving/copying files or directories and the `GitMessageHandler` prints info, warnings and debug output to the log. The dependencies between these classes are shown in the figure below, where the number on each dependency denotes the number of references.

<img src="img/gitUpdaterDependencies.png" width="444px" height="232px" />

Before the refactoring, the line coverage of the `GitUpdater` class was only ..., but this has been improved to an overall coverage of ... The coverage of each new class derived from the old `GitUpdater` is displayed below.

<test coverage git>


### Package Dependency Cycle
When packages depend on eachother in a cycle they violate the Acyclic Dependency Principle (ADP). Such a cyclic dependency existed between the `.service.abstractmetric` and the `.service.metricactivator` packages. Another existed between the `.service.abstractmetric` and the `.service.pa` packages. These cycles can also be seen in the image below. 

<img src="img/abstractmetric-before.png" width="800px" height="380px" />

To fix these cycles we have merged the `.service.abstractmetric` and the `.service.metricactivator` packages and moved `PluginAdmin` interface and `PluginInfo` class to the `.service.abstractmetric` package. The result can be seen in the image below.

<img src="img/abstractmetric-after.png" width="1020px" height="310px" />

These changes are a good idea, because now the amount of cyclic dependencies is reduced. With is fix we didn't introduce new cyclic dependencies and there are no cyclic dependencies in the `.service.abstractmetric` package. The package did become larger, but the four added files have a lot to do with the most important class `AbstractMetric`.

### Class Dependency Cycle in `.service.fds`
Cyclic dependencies can also exist between classes, which also violates the ADP. The dependency between the `InMemoryDireectory` class and the `InMemoryCheckout` interface was a cyclic one. All the dependencies in the `.service.fds` package are visible in the image below. 

<img src="img/fds-before.png" width="705px" height="344px" />

In order to break the cycle we have removed the dependency from `InMemoryDirectory` to `InMemoryCheckout`.  To make this possible we have removed some functionality from `InMemoryDirectory`, which is now available in the `InMemoryCheckoutImpl` class which implements the `InMemoryCheckout` interface. The resulting package structure, after the changes to its structure, is displayed in the image below.

<img src="img/fds-after.png" width="693px" height="241px" />

### Class Dependency Cycle in `.service.db`

In the `.service.db` a very large tangle of classes can be found, as many as 38 classes depend on each other. To untangle these classes we have removed some dependencies between classes. 

Between `Tag` and `StoredProject` was a cyclic dependency, this was due a two static methods in `Tag` that were called from `StoredProject` and needed a `StoredProject` as parameter. These two methods have been moved to `StoredProject` and are now non-static. This also follows the Object Oriented Programming way of programming better.  

## Bug Fixes

### SchedulerStats
In `eu.sqooss.service.scheduler.SchedulerStats.java` we found a few bugs in the `removeWaitingJob(String classname)` method. The old code, which included these bugs, is visible below. 

    public synchronized void removeWaitingJob(String classname) {
	    this.waitingJobs --;
	    if (waitingJobTypes.containsKey(classname)) {
		    int jobs = waitingJobTypes.get(classname) - 1;
		    if (jobs == 0) {
			    waitingJobTypes.remove(classname);
		    }

		    waitingJobTypes.put(classname, jobs);
	    }
    }

This code does not make much sense, because now the amount of waiting jobs (`waitingJobs`) can become negative. Also the check "`if (jobs == 0)`" is completely useless, because what will be removed from the map will be put back right away. Therefore we changed it to the following, which will change the behavior of the code, but makes much more sense. 

	public synchronized void removeWaitingJob(String classname) {
		if (waitingJobTypes.containsKey(classname)) {
			this.waitingJobs--;
			int jobs = waitingJobTypes.get(classname) - 1;
			if (jobs <= 0) {
				waitingJobTypes.remove(classname);
			} else {
				waitingJobTypes.put(classname, jobs);
			}
		}
	}

## Recommendations 
