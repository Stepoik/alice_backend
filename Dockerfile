FROM openjdk:11-jre-slim

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем JAR-файл в контейнер
COPY ../build/libs/app.jar app.jar

# Указываем команду для запуска JAR-файла
ENTRYPOINT ["java", "-jar", "app.jar"]