(1)在src下建立db.properties
(2)每张表只能有一个主键，不能处理多个主键的情况
(3)po尽量使用包装类，不要使用基本数据类型。可以帮助识别是否为null
(4)目前，只能处理数据库来维护自增主键的方式,其实，当数据库来维护自增主键时，你添加也添加不进去
