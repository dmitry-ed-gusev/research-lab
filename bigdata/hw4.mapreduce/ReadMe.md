### DataSet
* [data](http://goo.gl/lwgoxw) 
    * Work only with this files:
        - imp.20131019.txt.bz2
        - imp.20131020.txt.bz2
        - imp.20131021.txt.bz2
        - imp.20131022.txt.bz2
        - imp.20131023.txt.bz2
        - imp.20131024.txt.bz2
        - imp.20131025.txt.bz2
        - imp.20131026.txt.bz2
        - imp.20131027.txt.bz2

- [More detailed data description](http://contest.ipinyou.com/ipinyou-dataset.pdf)
    - The impression, click and conversion log data format.

    SN      |Column     |Example
    --------|-----------|--------:
    ∗1      |BidID          | 01530000008a77e7ac18823f5a4f5121   
    2       |Timestamp      | 20130218134701883
    3       |LogType        | 1
    ∗4      |iPinYouID      | 35605620124122340227135
    5       |User-Agent     | Mozilla/5.0 (compatible; MSIE 9.0;\Windows NT 6.1; WOW64; Trident/5.0)
    ∗6      |IP             | 118.81.189.
    ∗7      |RegionID       | 15
    8       |CityID         | 16
    9       |AdExchange     | 2
    ∗10     |Domain         | e80f4ec7f5bfbc9ca416a8c01cd1a049
    ∗11     |URL            | hz55b000008e5a94ac18823d6f275121
    12      |Anonymous URL  | null
    13      |Ad Slot ID     | 2147689...8764813
    14      |Ad Slot Width  | 300
    15      |Ad Slot Height | 250
    16      |Ad Slot Visibility| SecondView
    17      |Ad Slot Format |Fixed
    18      |Ad Slot Floor Price    |   0
    19      |Creative ID    | e39e178ffdf366606f8cab791ee56bcd
    ∗20     |Bidding Price  | 753
    ∗21     |Paying Price   | 15
    ∗22     |Landing Page URL   |   a8be178ffdf366606f8cab791ee56bcd
    23      |   Advertiser ID   |3358
    ∗24     |User Profile IDs   | 123,5678,3456

Columns with * means the data in the column is hashed or modified before the log is released
# Tasks:

1. Unzip these files and put them in HDFS system and make screenshot #1 of HDFS files:
2. Calculate amount of high-bid-priced  **(more than 250)** impression events by city
3. Print result where each city presented with its name rather than id
4. Make screenshot of execution #2 and results #3 from HDFS
5. Add Custom Partitioner by OperationSystemType
6. Run with several reducers and make screenshot of successful job #4 and results from HDFS #5


# Expected outputs:
	0. ZIP-ed src folder with your implementation
	1. Screenshot of successfully executed tests
	2. Screenshot #1 of successfully uploaded file into HDFS
	3. Screenshot #2, #3 successfully executet job and result.
	3. Screenshot #4, #5 successfully executet job wit several reducers and result.

# Acceptance criteria
    - Map Reduce API is used
    - Custom Type is defined and used
    - Custom Partitioner is used
    - Number of Reducers more than 1
