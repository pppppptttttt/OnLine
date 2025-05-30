CREATE TABLE IF NOT EXISTS OnLine.users
(
    user_id  uuid PRIMARY KEY,
    username text,
    email    text,
    friends  list<uuid>
);

CREATE TABLE IF NOT EXISTS OnLine.email_to_user_id
(
    email   text PRIMARY KEY,
    user_id uuid
);

CREATE TABLE IF NOT EXISTS OnLine.user_statistics
(
    user_id   uuid,
    timestamp date,
    name      text,
    value     double,
    PRIMARY KEY ((user_id), name, timestamp)
) WITH CLUSTERING ORDER BY (name ASC, timestamp DESC);

CREATE TABLE IF NOT EXISTS OnLine.paths
(
    user_id   uuid,
    path_id   uuid,
    polyline  text,
    created   date,
    name      text,
    distance  double,
    duration  double,
    PRIMARY KEY ((user_id), path_id)
);