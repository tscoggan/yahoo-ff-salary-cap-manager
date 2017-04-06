# Yahoo Fantasy Football Salary Cap Manager

This is a Scala application that adds support for salary cap-based player contract management to an existing [Yahoo Fantasy Football](https://football.fantasysports.yahoo.com/) league.  It is intended for franchise (keeper) leagues in which there is some degree of roster continuity across multiple seasons.  The basic premise is as follows:
- each team has a fixed salary cap (i.e. budget to spend on players) per season --- this is typical of auction draft leagues but may also be implemented on top of a standard snaking draft
- each drafted player is signed to a contract --- the rules governing contracts (e.g. max length in years, date by which contracts must be signed, how "dead money" for released players is handled, etc) will be configurable per league

This app is **not** intended to replace the core functionality (e.g. drafting, roster management, live scoring, etc) supported by the Yahoo Fantasy Football site but rather to compliment it.  

## Design

- Scala web application --- choice of web framework TBD
- Embedded H2 relational DB for data persistence (e.g. player contracts)
- Communicates with [Yahoo Fantasy Sports API](https://developer.yahoo.com/fantasysports/guide/) using [YQL](https://developer.yahoo.com/yql/) queries to download league data (teams, players, transactions, etc)

## Current State

This app is very early in its development and is not yet a full working prototype!
