# This file is autogenerated. Instead of editing this file, please use the
# migrations feature of ActiveRecord to incrementally modify your database, and
# then regenerate this schema definition.

ActiveRecord::Schema.define(:version => 3) do

  create_table "access_codes", :force => true do |t|
    t.column "organization", :string
    t.column "code",         :string
  end

  create_table "users", :force => true do |t|
    t.column "email",             :string,   :limit => 80
    t.column "password_hash",     :string,   :limit => 80
    t.column "password_salt",     :string,   :limit => 80
    t.column "organization",      :string,   :limit => 80
    t.column "position",          :string,   :limit => 80
    t.column "created_timestamp", :datetime
    t.column "access_code",       :string,   :limit => 80
    t.column "notes",             :string,   :limit => 500
    t.column "admin_flag",        :boolean
  end

end
