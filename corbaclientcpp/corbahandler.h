#ifndef CORBAHANDLER_H
#define CORBAHANDLER_H

#include <CORBA.h>

class CorbaHandler
{
protected:
    CorbaHandler();

private:
    CorbaHandler( const CorbaHandler& ) {}
    void operator=( const CorbaHandler& );

public:
    virtual ~CorbaHandler();

    static CorbaHandler* instance();

    CORBA::Object_var getObject( const char* className ) const;

private:
    CORBA::ORB_var orb;
};

#endif
