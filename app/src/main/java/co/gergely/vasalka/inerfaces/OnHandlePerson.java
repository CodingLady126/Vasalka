package co.gergely.vasalka.inerfaces;

import co.gergely.vasalka.model.Person;
import co.gergely.vasalka.model.SearchPerson;

import java.util.List;

public interface OnHandlePerson {
    void onPersonClicked(Person person);
    void onPersonClicked(SearchPerson person);

    void onShowSearchResult(List<SearchPerson> searchResultList);
}
