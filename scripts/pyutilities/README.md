# [pyutilities] package

**Useful Python 2.7 utilities.**  
*Last update 11.02.2019*

For content here I use 
[Github-flavored Markdown](https://guides.github.com/features/mastering-markdown/).

**Versions history**  
0.1.0  
Initial version. Just draft of utilities library.
  
0.2.0  
Added tests and some new methods.  

0.3.0  
Added ConfigurationXls class. It extends (inherites) Configuration class with ability of
loading configuration from XLS files, from specified sheet, as name=value pairs. Added some
unit tests for new class.  
Added dependencies list: requirements.txt file.

0.3.1  
Minor fixes in ConfigurationXls: added support for parent class parameters.

0.4.0  
Added ability for Configuration class to merge list of dictionaries on init. Minor improvements,
added several unit test cases. Minor refactoring.

0.5.0  
Added ability for ConfigurationXls class to merge provided list of dictionaries on init. Added more 
unit test cases for ConfigurationXls class (initialization, dictionaries merge).

0.5.1  
Minor typo fixes in README.

0.5.2  
README format fixes :)

0.5.3  
Added one utility method - write_report_to_file(). Minor fixes, comments improvements.

0.5.4  
Added method contains_key() to Configuration class.
