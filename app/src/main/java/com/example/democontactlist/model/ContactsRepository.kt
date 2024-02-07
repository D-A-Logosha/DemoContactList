package com.example.democontactlist.model

import kotlin.random.Random

typealias ContactsListener = (contacts: List<Contact>) -> Unit

class ContactsRepository {

    private var contacts = mutableListOf<Contact>()

    private val listeners = mutableSetOf<ContactsListener>()

    init {
        contacts = (1..100).map {
            val firstName: String
            var lastName: String

            if (Random.nextBoolean()) {
                firstName = maleFirstName[Random.nextInt(0, maleFirstName.lastIndex)]
                lastName = maleLastName[Random.nextInt(0, maleFirstName.lastIndex)]
            } else {
                firstName = femaleFirstName[Random.nextInt(0, maleFirstName.lastIndex)]
                lastName = femaleLastName[Random.nextInt(0, maleFirstName.lastIndex)]
            }
            var phoneNumber: String = randomPhoneNumber()

            if (it == 3) lastName += "\n не забыть позвонить 12.11.2024 и напомнить о каточном долге"
            if (it == 4) phoneNumber += "\n" + randomPhoneNumber()
            if (it == 13) lastName += "\n\t день рождения 29.02.1999\n" + "\t жаль что приложение контактов не синхронизируется с календарём"
            if (it == 19) {
                lastName += "\n" + "\t 2я строка \n" + "\t 3я строка \n" + "\t 4я строка \n" + "\t 5я строка \n" + "\t 6я строка \n" + "\t 7я строка "
                phoneNumber += "\n" + randomPhoneNumber() + "\n" + randomPhoneNumber()
            }

            Contact(
                id = it, firstName = firstName, lastName = lastName, phoneNumber = phoneNumber
            )
        }.toMutableList()
    }


    fun get(): List<Contact> {
        return contacts
    }

    fun getSize(): Int {
        return contacts.size
    }

    fun addContact(contact: Contact) {
        if (contact.id == 0) contacts.add(contact.copy(id = contacts[contacts.lastIndex].id + 1))
        notifyChanges()
    }

    fun editContact(contact: Contact) {
        val indexToEdit = contacts.indexOfFirst { it.id == contact.id }
        if (indexToEdit != -1) {
            contacts[indexToEdit] = contact
            notifyChanges()
        }
    }

    fun deleteContact(contact: Contact) {
        val indexToDelete = contacts.indexOfFirst { it.id == contact.id }
        if (indexToDelete != -1) {
            contacts = ArrayList(contacts)
            contacts.removeAt(indexToDelete)
            notifyChanges()
        }
    }

    fun addListener(listener: ContactsListener) {
        listeners.add(listener)
        listener.invoke(contacts)
    }

    fun removeListener(listener: ContactsListener) {
        listeners.remove(listener)
    }

    private fun notifyChanges() {
        listeners.forEach { it.invoke(contacts) }
    }
}

private fun randomPhoneNumber() =
    "+7" + " (${Random.nextInt(300, 999)}) " +
            "%03d".format(Random.nextInt(999)) + "-" +
            "%02d".format(Random.nextInt(99)) + "-" +
            "%02d".format(Random.nextInt(99))

private val maleFirstName = listOf(
    "Максим", "Михаил", "Александр", "Дмитрий", "Денис", "Илья", "Андрей", "Даниил", "Артём",
    "Иван", "Алексей", "Никита", "Павел", "Евгений", "Антон", "Лев", "Эльдар", "Григорий",
    "Владимир", "Руслан", "Василий", "Виталий", "Вячеслав", "Игнат", "Николай", "Олег", "Ренат",
    "Роман", "Сергей", "Тимур", "Богдан", "Гарик", "Давид", "Камиль", "Кирилл", "Константин",
    "Леонид", "Матвей", "Степан", "Филипп", "Аркадий", "Вадим", "Виктор", "Георгий", "Егор",
    "Макар", "Семён", "Станислав", "Тимофей", "Юрий", "Всеволод"
)
private val maleLastName = listOf(
    "Агеев", "Аксенов", "Александров", "Александров", "Андреев", "Андреев", "Андреев", "Афанасьев",
    "Басов", "Белов", "Белов", "Беляев", "Бобров", "Васильев", "Волков", "Волков", "Волков",
    "Волков", "Голиков", "Горбунов", "Давыдов", "Демьянов", "Дмитриев", "Дубровин", "Дьяконов",
    "Елисеев", "Ермолов", "Ефремов", "Жуков", "Зайцев", "Захаров", "Зиновьев", "Зиновьев",
    "Золотарев", "Иванов", "Калинин", "Калинин", "Карасев", "Карасев", "Карпов", "Карпов",
    "Касаткин", "Климов", "Климов", "Климов", "Коновалов", "Кононов", "Корчагин", "Кузнецов",
    "Кулагин", "Кулешов", "Куликов", "Лавров", "Лазарев", "Ларин", "Лебедев", "Лебедев", "Лебедев",
    "Логинов", "Максимов", "Медведев", "Медведев", "Михайлов", "Михайлов", "Молчанов", "Никитин",
    "Николаев", "Орлов", "Орлов", "Осипов", "Осипов", "Павлов", "Петров", "Петухов", "Пименов",
    "Пирогов", "Попов", "Попов", "Попов", "Романов", "Рыжов", "Свиридов", "Седов", "Семенов",
    "Сергеев", "Ситников", "Смирнов", "Соболев", "Сорокин", "Степанов", "Суворов", "Терехов",
    "Терехов", "Титов", "Трофимов", "Федотов", "Филатов", "Черкасов", "Черняев", "Яковлев"
)
private val femaleFirstName = listOf(
    "Анна", "Мария", "Юлия", "Алёна", "Анастасия", "Екатерина", "Дарья", "Ксения", "Кристина",
    "Алиса", "Яна", "Ольга", "Александра", "Светлана", "Елизавета", "Маргарита", "Елена", "Агата",
    "Юлиана", "Ирина", "Алина", "Арина", "Валерия", "Виктория", "Диана", "Ева", "Карина",
    "Каролина", "Марина", "Наталья", "Варвара", "Василиса", "Вера", "Любовь", "Марьяна", "Надежда",
    "Оксана", "Регина", "Софья", "Татьяна", "Алла", "Ангелина", "Вероника", "Евгения", "Жанна",
    "Лилия", "Милана", "Полина", "Рената", "Эльвира"
)
private val femaleLastName = listOf(
    "Агафонова", "Ананьева", "Андреева", "Архипова", "Архипова", "Афанасьева", "Белякова",
    "Блохина", "Богданова", "Бочарова", "Васильева", "Виноградова", "Владимирова", "Владимирова",
    "Волошина", "Воронова", "Герасимова", "Гончарова", "Гордеева", "Горшкова", "Грачева",
    "Григорьева", "Григорьева", "Денисова", "Денисова", "Дмитриева", "Дорохова", "Егорова",
    "Емельянова", "Ермолова", "Захарова", "Зиновьева", "Зиновьева", "Иванова", "Игнатьева",
    "Калинина", "Карпова", "Касаткина", "Киселева", "Колесникова", "Колпакова", "Комиссарова",
    "Корнилова", "Королева", "Короткова", "Короткова", "Крылова", "Кузнецова", "Кузнецова",
    "Кузнецова", "Кузнецова", "Кузнецова", "Кузьмина", "Лаптева", "Ларина", "Лебедева",
    "Леонтьева", "Логинова", "Лукина", "Львова", "Макарова", "Макарова", "Минина", "Михайлова",
    "Михайлова", "Морозова", "Некрасова", "Никитина", "Никитина", "Никитина", "Никифорова",
    "Никифорова", "Николаева", "Овчинникова", "Орлова", "Пантелеева", "Попова", "Попова", "Попова",
    "Романова", "Руднева", "Савельева", "Семенова", "Сергеева", "Сидорова", "Смирнова", "Соболева",
    "Сорокина", "Субботина", "Суворова", "Титова", "Тихонова", "Федорова", "Филиппова", "Филиппова",
    "Хомякова", "Хохлова", "Чеботарева", "Шувалова", "Яковлева"
)