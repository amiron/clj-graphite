# clj-graphite

A very simple Clojure library for accessing data in Graphite using the render API

## Usage

### Retrieving raw metric data

    (require '[graphite.render :as render])

    @(render/render "http://server.net"
                   "some.series"
                   {:from "10:00_20150101"}

### Retrieving function call results

    (require '[graphite.render :as render])

    @(render/render "http://server.net"
                   (-> "some.metric"
                       (render/func :summarize "1hour" "avg")
                       (render/func :removeBelowValue 10))
                   {:from "10:00_20150101"}

Will translate into the following function call:
removeBelowValue(summarize("some.metric", "1hour", "avg"), 10)

### Return value

The render function returns a Manifold (https://github.com/ztellman/manifold) deferred.
If you don't care about it, just deref and use the value.

## License

Copyright © 2015

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
