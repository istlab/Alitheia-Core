#ifndef DATABASE_H
#define DATABASE_H

namespace Alitheia
{
    class DAObject;

    class Database
    {
    public:
        Database();
        virtual ~Database();

        bool addRecord( const DAObject& object );

    private:
        class Private;
        Private* const d;
    };
}

#endif
