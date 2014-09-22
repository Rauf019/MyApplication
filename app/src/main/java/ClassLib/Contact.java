package ClassLib;

public class Contact {
    String _Name;
    String _phoneNumber;

    boolean _is_Call_block, _is_Msg_block;


    public Contact(String _phoneNumber, String _Name, boolean _is_Call_block,
                   boolean _is_Msg_block) {

        this._phoneNumber = _phoneNumber;
        this._Name = _Name;
        this._is_Call_block = _is_Call_block;
        this._is_Msg_block = _is_Msg_block;

    }

    public Contact() {
        // TODO Auto-generated constructor stub
    }


    public String get_Name() {
        return _Name;
    }

    public void set_Name(String _Name) {
        this._Name = _Name;
    }

    public String get_phoneNumber() {
        return _phoneNumber;
    }

    public void set_phoneNumber(String _phoneNumber) {
        this._phoneNumber = _phoneNumber;
    }

    public boolean get_is_Call_block() {
        return _is_Call_block;
    }

    public void set_is_Call_block(boolean string) {
        this._is_Call_block = string;
    }

    public boolean get_is_Msg_block() {
        return _is_Msg_block;
    }

    public void set_is_Msg_block(boolean _is_Msg_block) {
        this._is_Msg_block = _is_Msg_block;
    }


}
