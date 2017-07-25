package ru.enfester;

public class Config {

    public static String Version = "30.12.2016 6:10"; // Версия лаунчера

    public static String Title = "Enfester Launcher minecraft. Версия от " + Version; // Название лаунчера

    public static String ClentDir = ".enfester"; // Имя папки с клиентом
    public static String LogName = "log.txt"; // Имя лог файла
    //public static String DownloadDir = "client"; // Имя папки откуда кчается клиент

    public static String SystemDir = "https://enserver.ru/api/"; // Путь к папке для лаунчера
    public static String RestorePassDir = "http://enserver.ru/password/reset"; // Ссылка для востановления пароля


    // public static File fileSave = new File(utils.getMcDir(ClentDir) + File.separator + "launcher.ini");
    /**
     * Сервера
     */
    public static String[][] Servers = {
        {
            "Classic", // Имя папки клиента и название
            "46.147.193.33", // Ip
            "25567", // Port
            "1.7.10", // Версия клиента
            "true", // Обновление клиента
            "Данный сервер в разработке. Дата открытия неизвестна.", // Описание клиента
            "shadersmodcore.jar" // Название файла шейдеров *(Доделать массив файлов исключения обновления)
        },
        //        {
        //            "Magic",
        //            "mc01.serveromat.net",
        //            "26005",
        //            "1.7.10",
        //            "true",
        //            "Магический сервер.",
        //            "shadersmodcore.jar"
        //        },
        //        {
        //            "RPG",
        //            "localhost",
        //            "25565",
        //            "1.7.10",
        //            "false",
        //            "Данный сервер в разработке. Дата открытия неизвестна.",
        //            "shadersmodcore.jar"
        //        },
        {
            "Industrial",
            "151.80.71.132",
            "25830",
            "1.7.10",
            "true",
            "Industrial Сервер с повышенным режимом сложности, который заставит тебя вспотеть во время игры! Кучу монстров держат тебя в напряжении от того, что в любой момент можешь потерять все!",
            "shadersmodcore.jar"
        }
    };

}
