# Social Network Analysis - Communities and User Importance

## Introduction
In this project, I was interested in learning about structures within social networks. Among millions of users, can we identify communities? Can we find out which users have a special role or are especially important within these communities? Follow along and we will try to assess how important users are in a network of over 80000 Twitter users, based on their [in-degree centrality](https://en.wikipedia.org/wiki/Centrality#Degree_centrality) and [Page-Rank](https://en.wikipedia.org/wiki/PageRank) score. In the second part, we will try to identify communities within the Facebook data of 783 UC San Diego students, using the [Girvan-Newmann algorithm](https://en.wikipedia.org/wiki/Girvan%E2%80%93Newman_algorithm).

Of cource I'm not the first person asking these questions and there great libraries already out there, but since I worked on this project as part of an open-ended capstone of the MOOC specialization *Object Oriented Java Programming: Data Structures and Beyond* on [Coursera.org](https://www.coursera.org/specializations/java-object-oriented), I coded up the algorithms from scratch in Java and used NetworkX in Python to test my code.

## Table of contents
* [Introduction](#introduction)
* [Analysis](#analysis)
* [Class overview](#class-overview)
* [Testing](#testing)
* [Acknowledgement](#acknowledgement)

## Analysis
You can also read this part with the actual code I used in this [Jupyter Notebook](NetworkAnalysis.ipynb).
### Important users on Twitter
Twitter is an interesting network to study because it can be represented by a directed graph. Not all users who follow someone are being followed back. Some important or influential people may have a lot more followers, meaning a lot more incoming edges. 

That's something that can be easily quantified, e.g. by a user's in-degree centrality (how many followers?). But maybe one wants to consider certain users as more important if they are followed by other important users. That's the idea of Google's famous Page-Rank algorithm which considers how much of its own score each user contributes to the score of another user. Both these scores are computed by the Java code in this project.

The anonymous Twitter data we have here is is a social circle data set from the SNAP database and can be found [here](https://snap.stanford.edu/data/ego-Twitter.html). The graph contains 81306 vertices and 1768149 edges.

After claculating both metrics (stored in this [file](data/Analysis/twitterIDCPR.txt)) and scaling them on the same scale (between 0 and 100), we can observe that almost all users have a very low in-degree centrality and Page-Rank score and only very few users have very high scores. That agrees with the intuition that most users are just "normal people" who use the network to connect with their friends and only very few users can be considered as influential or important individuals. We can also see that the differences between in-degree centrality and Page-Rank score are very small. Since most users have very low scores, contributions from "important" users to other "important" user which make them even more important (the basic idea of the Page-Rank algorithm), seem to be practically irrelevant here. Both these observations, low scores for most users, and almost no difference between these score metrics, become clearer when they are plotted (on a log scale): ![userIDCPR](data/Figures/userIDCPR.png | width=100)


## Class overview

## Testing

## Acknowledgement

