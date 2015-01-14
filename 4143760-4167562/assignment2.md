# IN4189 Software Reengineering - Testing and Refactoring Report
By Martijn den Hoedt - 4143760 and Anton Bouter - 4167562

## Tests

## Refactoring

### Package Dependency Cycle
Between the `.service.abstractmetric` and the `.service.metricactivator` packages was a cyclic dependency. Also between the `.service.abstractmetric` and the `.service.pa` package was a cyclic dependency. These cycles can also be seen in the image below. 

<img src="img/before.png" width="800px" height="380px" />

To fix these cycles we have merged the `.service.abstractmetric` and the `.service.metricactivator` packages and moved `PluginAdmin` interface and `PluginInfo` class to the `.service.abstractmetric` package. The result can be seen in the image below.

<img src="img/after.png" width="1020px" height="310px" />

These changes are a good idea, because now the amount of cyclic dependencies is reduced. With is fix we didn't introduce new cyclic dependencies and there are no cyclic dependencies in the `.service.abstractmetric` package. The package did become larger, but the four files added have a lot to do with the most important class `AbstractMetric`.

## Recommendations 
