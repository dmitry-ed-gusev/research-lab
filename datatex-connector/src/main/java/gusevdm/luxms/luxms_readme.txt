Structure of LuxMS DB:
    What?  -> table [metrics]   -> key [metric_id] => table [units]
    Where? -> table [locations] -> key [loc_id]
    When?  -> table [periods]   -> key [period_id] => table [period_types]

Metric JSON:
    {
        "id": 11,
        "title": "Performance",
        "tree_level": 1,   // <- for root tree_level=0
        "parent_id": null, // <- for root parent_id=null
        "is_hidden": 0,
        "unit_id": 2,
        "srt": 10          // <- sorting in UI
    }

Unit JSON:
    {
        "id": 1,
        "title": "m/hour",
        "value_prefix": "$",
        "value_suffix": "%",
        "tiny_title": "øò.",
        "axis_title": "some-title"
    }

Location JSON:
    {
        "id": 11,
        "title": "Equipment #1",
        "tree_level": 1,
        "parent_id": null,
        "is_hidden": 0,
        "latitude": 37.61556,
        "longitude": 55.75222,
        "srt": 10
    }

Period JSON:
    {
        "id": "337719944274378750",
        "title": "II quarter 2015",
        "start_time" : "2015-12-01",  // <- SQL date
        "period_type":6
    }
Periods types:
1 -> seconds
2 -> minutes
3 -> hours
4 -> days
5 -> weeks
6 -> months
7 -> quarters
8 -> years