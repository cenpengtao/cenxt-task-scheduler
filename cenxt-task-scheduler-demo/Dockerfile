FROM java:8
# 维护者信息
MAINTAINER cenpengtao "cpt725@qq.com"
RUN mkdir -p /appdata/cenxt-task-scheduler-demo/
ADD dist/*.tar.gz /appdata/cenxt-task-scheduler-demo/
ENV SPRING_PROFILES_ACTIVE prod
ENV TZ 'Asia/Shanghai'
EXPOSE 8070
CMD sh /appdata/appdata/cenxt-task-scheduler-demo/bin/appdata/cenxt-task-scheduler-demo.sh start