pragma solidity ^0.4.24;

contract Example {

    string _name;

    function getName() public view returns(string) {
        return _name;
    }

    function setName(string name) public {
        _name = name;
    }
}